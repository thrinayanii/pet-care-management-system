document.addEventListener('DOMContentLoaded', async () => {
    if (document.getElementById('volNameWelcome') || document.getElementById('calendarDays') || document.getElementById('shiftsTableBody')) {
        await loadVolunteerDashboard();
    }
    if (document.getElementById('inquiryForm')) {
        setupInquiryForm();
    }
});

let userApplications = [];
let currentDate = new Date();

async function loadVolunteerDashboard() {
    try {
        const res = await fetch('get_volunteer.php');
        const data = await res.json();

        if (data && data.error) {
            console.warn("Auth check:", data.error);
        }

        // 1. Render User's Name
        if (data && data.user && data.user.first_name) {
            const userName = data.user.first_name;
            const welcomeEl = document.getElementById('volNameWelcome');
            const navNameEl = document.getElementById('profileNavName');
            
            if (welcomeEl) welcomeEl.textContent = userName;
            if (navNameEl) navNameEl.textContent = userName;
        }

        // 2. Read Shift List
        const shiftsList = (data && (data.shifts || data.applications)) ? (data.shifts || data.applications) : [];
        userApplications = shiftsList;

        // 3. Render Stats (If on Dashboard)
        if (document.getElementById('statAvailableTasks')) {
            document.getElementById('statAvailableTasks').textContent = (data && data.tasks) ? data.tasks.length : 0;
        }
        if (document.getElementById('statMyApps')) {
            document.getElementById('statMyApps').textContent = shiftsList.length;
        }
        if (document.getElementById('statApproved')) {
            document.getElementById('statApproved').textContent = (data && data.approved_count) ? data.approved_count : 0;
        }

        // 4. Render Tasks & Calendar (Dashboard)
        renderTasks((data && data.tasks) ? data.tasks : []);
        renderCalendar(currentDate);
        setupCalendarNavigation();

        // 5. Render "My Shifts" Table (volunteer_applications.html)
        renderShiftsTable(shiftsList);

    } catch (err) {
        console.error("Error loading volunteer data:", err);
        renderCalendar(currentDate);
        setupCalendarNavigation();
    }
}

function renderTasks(tasks) {
    const container = document.getElementById('availableTasksList');
    if (!container) return;

    if (!tasks || tasks.length === 0) {
        container.innerHTML = `<p style="color: #64748b; padding: 1rem;">No available tasks at the moment.</p>`;
        return;
    }

    container.innerHTML = tasks.map(task => `
        <div class="dash-pet-item">
            <div class="dash-pet-avatar">
                <span class="material-symbols-outlined">${task.icon || 'pets'}</span>
            </div>
            <div class="dash-pet-info">
                <h4>${task.title} ${task.is_preferred ? '<span style="font-size:12px; background:#dcfce7; color:#166534; padding:2px 8px; border-radius:12px; font-weight:600;">Recommended</span>' : ''}</h4>
                <p><strong>Date:</strong> ${task.task_date}</p>
                <p><strong>Time:</strong> ${task.time}</p>
                <p><strong>Location:</strong> ${task.location}</p>
            </div>
            <button class="btn-primary apply-btn" onclick="applyTask(${task.task_id}, this)">Request Shift</button>
        </div>
    `).join('');
}

function renderShiftsTable(shifts) {
    const tableBody = document.getElementById('shiftsTableBody');
    if (!tableBody) return;

    if (!shifts || shifts.length === 0) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align: center; color: #64748b; padding: 20px;">
                    No requested shifts yet. Head over to the Dashboard to request available shifts!
                </td>
            </tr>`;
        return;
    }

    tableBody.innerHTML = shifts.map(shift => {
        const isApproved = shift.status && shift.status.toLowerCase() === 'approved';
        const statusClass = isApproved ? 'approved' : 'pending';
        
        const actionBtn = isApproved 
            ? `<button type="button" onclick="viewShiftDetails('${shift.task_title}', '${shift.status}', '${shift.date}', '${shift.location}')" 
                       style="padding: 6px 14px; font-size: 13px; font-weight: 600; color: #1e293b; background-color: #f1f5f9; border: 1px solid #cbd5e1; border-radius: 6px; cursor: pointer;">
                 View Details
               </button>`
            : `<button type="button" onclick="cancelShiftRequest(${shift.id})" 
                       style="padding: 6px 14px; font-size: 13px; font-weight: 600; color: #dc2626; background-color: #fef2f2; border: 1px solid #fca5a5; border-radius: 6px; cursor: pointer;">
                 Cancel Request
               </button>`;

        return `
            <tr>
                <td><strong>${shift.task_title || 'Shift'}</strong></td>
                <td>${shift.date || '-'}</td>
                <td>${shift.location || 'Shelter Facility'}</td>
                <td><span class="status ${statusClass}">${shift.status}</span></td>
                <td>${actionBtn}</td>
            </tr>
        `;
    }).join('');
}

async function cancelShiftRequest(requestId) {
    if (!confirm("Are you sure you want to cancel this shift request?")) return;

    const formData = new FormData();
    formData.append('action', 'cancel');
    formData.append('id', requestId);

    try {
        const res = await fetch('process_task_action.php', { method: 'POST', body: formData });
        const result = await res.json();

        if (result && result.success) {
            alert("Shift request cancelled successfully.");
            location.reload();
        } else {
            alert((result && result.error) || "Failed to cancel request.");
        }
    } catch (e) {
        alert("An error occurred while cancelling.");
    }
}

function viewShiftDetails(title, status, date, location) {
    const modal = document.getElementById('reasonModal');
    if (!modal) return;
    
    document.getElementById('modalTaskName').textContent = title;
    document.getElementById('modalTaskStatus').textContent = status;
    document.getElementById('modalTaskReason').textContent = `Your shift is scheduled for ${date} at ${location}. Please arrive 10 minutes prior to your start time.`;
        
    modal.style.display = 'flex';
}

async function applyTask(taskId, btn) {
    btn.disabled = true;
    btn.textContent = "Requesting...";

    const formData = new FormData();
    formData.append('action', 'apply');
    formData.append('id', taskId);

    try {
        const res = await fetch('process_task_action.php', { method: 'POST', body: formData });
        const result = await res.json();

        if (result && result.success) {
            btn.textContent = "Requested";
            btn.style.opacity = ".7";
            alert("Shift requested successfully! Awaiting admin confirmation.");
            location.reload();
        } else {
            btn.disabled = false;
            btn.textContent = "Request Shift";
            alert((result && result.error) || "Failed to request shift.");
        }
    } catch (e) {
        btn.disabled = false;
        btn.textContent = "Request Shift";
        alert("An error occurred. Please try again.");
    }
}

function renderCalendar(date) {
    const monthYear = document.getElementById("monthYear");
    const calendarDays = document.getElementById("calendarDays");
    if (!calendarDays || !monthYear) return;

    calendarDays.innerHTML = "";
    const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
    const year = date.getFullYear();
    const month = date.getMonth();

    monthYear.textContent = `${months[month]} ${year}`;

    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        const blank = document.createElement("div");
        blank.className = "empty";
        calendarDays.appendChild(blank);
    }

    for (let day = 1; day <= totalDays; day++) {
        const cell = document.createElement("div");
        cell.innerText = day;

        const matchingApp = userApplications.find(app => {
            if (!app.date) return false;
            const parsed = new Date(app.date);
            if (isNaN(parsed.getTime())) return false;
            return parsed.getDate() === day && parsed.getMonth() === month && parsed.getFullYear() === year;
        });

        if (matchingApp) {
            if (matchingApp.status && matchingApp.status.toLowerCase() === 'approved') {
                cell.classList.add("approved-day");
            } else {
                cell.classList.add("pending-day");
            }
            cell.title = `${matchingApp.task_title || 'Shift'}\nStatus: ${matchingApp.status}`;
        }

        calendarDays.appendChild(cell);
    }
}

function setupCalendarNavigation() {
    const prevBtn = document.getElementById("prevMonth");
    const nextBtn = document.getElementById("nextMonth");

    if (prevBtn) {
        prevBtn.onclick = () => {
            currentDate.setMonth(currentDate.getMonth() - 1);
            renderCalendar(currentDate);
        };
    }
    if (nextBtn) {
        nextBtn.onclick = () => {
            currentDate.setMonth(currentDate.getMonth() + 1);
            renderCalendar(currentDate);
        };
    }
}

function setupInquiryForm() {
    const inquiryForm = document.getElementById("inquiryForm");
    if (!inquiryForm) return;

    inquiryForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const formData = new FormData();
        formData.append('subject', document.getElementById("subject").value.trim());
        formData.append('category', document.getElementById("category").value);
        formData.append('message', document.getElementById("message").value.trim());

        try {
            const res = await fetch('process_inquiry.php', { method: 'POST', body: formData });
            const result = await res.json();

            if (result && result.success) {
                alert("Your inquiry has been submitted successfully.");
                inquiryForm.reset();
                location.reload();
            } else {
                alert((result && result.error) || "Failed to send inquiry.");
            }
        } catch (err) {
            alert("Error sending inquiry.");
        }
    });
}