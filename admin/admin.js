document.addEventListener('DOMContentLoaded', async () => {
    await loadAdminDashboard();
});

async function loadAdminDashboard() {
    try {
        const res = await fetch('get_admin.php');
        const data = await res.json();

        // 1. Stats Bar
        if (data.stats) {
            document.getElementById('statUsers').textContent = data.stats.total_users || 0;
            document.getElementById('statPendingShifts').textContent = data.stats.pending_shifts || 0;
            document.getElementById('statPendingAppts').textContent = data.stats.pending_appts || 0;
            document.getElementById('statRescuePets').textContent = data.stats.total_pets || 0;
        }

        // 2. Render Tables
        renderShiftRequests(data.shift_requests || []);
        renderAppointments(data.appointments || []);
        renderRescuePets(data.rescue_pets || []);

    } catch (err) {
        console.error("Error loading admin data:", err);
    }
}

function renderShiftRequests(requests) {
    const body = document.getElementById('shiftRequestsTable');
    if (!body) return;

    if (!requests || requests.length === 0) {
        body.innerHTML = `<tr><td colspan="5" style="text-align:center; padding:20px; color:#64748b;">No shift requests submitted yet.</td></tr>`;
        return;
    }

    body.innerHTML = requests.map(req => {
        const statusLower = req.status.toLowerCase();
        const statusClass = statusLower === 'approved' ? 'approved' : (statusLower === 'rejected' ? 'rejected' : 'pending');
        
        const isPending = statusLower === 'pending';
        const actionsHtml = isPending ? `
            <button onclick="handleShiftAction(${req.request_id}, 'Approved')" style="padding: 5px 12px; font-size: 13px; font-weight: 600; color: #15803d; background-color: #dcfce7; border: 1px solid #86efac; border-radius: 6px; cursor: pointer; margin-right: 6px;">Approve</button>
            <button onclick="handleShiftAction(${req.request_id}, 'Rejected')" style="padding: 5px 12px; font-size: 13px; font-weight: 600; color: #b91c1c; background-color: #fef2f2; border: 1px solid #fca5a5; border-radius: 6px; cursor: pointer;">Reject</button>
        ` : `<span style="color:#64748b; font-size:13px; font-weight: 500;">Completed</span>`;

        return `
            <tr>
                <td><strong>${req.volunteer_name}</strong></td>
                <td>${req.task_title}</td>
                <td>${req.date}</td>
                <td><span class="status ${statusClass}">${req.status}</span></td>
                <td>${actionsHtml}</td>
            </tr>
        `;
    }).join('');
}

function renderAppointments(appts) {
    const body = document.getElementById('appointmentsTable');
    if (!body) return;

    if (!appts || appts.length === 0) {
        body.innerHTML = `<tr><td colspan="4" style="text-align:center; padding:20px; color:#64748b;">No service appointments booked yet.</td></tr>`;
        return;
    }

    body.innerHTML = appts.map(appt => {
        const statusLower = appt.status.toLowerCase();
        const statusClass = statusLower === 'confirmed' || statusLower === 'completed' ? 'approved' : 'pending';

        return `
            <tr>
                <td><strong>${appt.owner_name}</strong></td>
                <td>${appt.service_name}</td>
                <td>${appt.date} at ${appt.time}</td>
                <td><span class="status ${statusClass}">${appt.status}</span></td>
            </tr>
        `;
    }).join('');
}

function renderRescuePets(pets) {
    const body = document.getElementById('rescuePetsTable');
    if (!body) return;

    if (!pets || pets.length === 0) {
        body.innerHTML = `<tr><td colspan="5" style="text-align:center; padding:20px; color:#64748b;">No rescue animals recorded.</td></tr>`;
        return;
    }

    body.innerHTML = pets.map(pet => `
        <tr>
            <td><strong>${pet.kennel_no || '-'}</strong></td>
            <td>${pet.name}</td>
            <td>${pet.species}</td>
            <td>${pet.breed}</td>
            <td><span class="status ${pet.status === 'available' ? 'approved' : 'pending'}">${pet.status}</span></td>
        </tr>
    `).join('');
}

async function handleShiftAction(requestId, newStatus) {
    if (!confirm(`Are you sure you want to set this shift request to ${newStatus}?`)) return;

    const formData = new FormData();
    formData.append('request_id', requestId);
    formData.append('status', newStatus);

    try {
        const res = await fetch('process_admin_action.php', { method: 'POST', body: formData });
        const result = await res.json();

        if (result && result.success) {
            alert(`Shift request ${newStatus.toLowerCase()} successfully!`);
            location.reload();
        } else {
            alert((result && result.error) || "Failed to update shift status.");
        }
    } catch (e) {
        alert("An error occurred during update.");
    }
}