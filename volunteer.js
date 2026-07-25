// ======================================
// Volunteer Dashboard JavaScript
// ======================================

// ---------- Calendar ----------

const monthYear = document.getElementById("monthYear");
const calendarDays = document.getElementById("calendarDays");

const months = [
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
];

let currentDate = new Date();

const approvedDays = [8, 15, 25];
const pendingDays = [12, 18];

function renderCalendar(date) {

    if (!calendarDays || !monthYear) return;

    calendarDays.innerHTML = "";

    const year = date.getFullYear();
    const month = date.getMonth();

    monthYear.textContent = `${months[month]} ${year}`;

    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        const blank = document.createElement("div");
        blank.classList.add("empty");
        calendarDays.appendChild(blank);
    }

    for (let day = 1; day <= totalDays; day++) {

        const cell = document.createElement("div");
        cell.textContent = day;

        if (approvedDays.includes(day)) {
            cell.classList.add("approved-day");
        }

        if (pendingDays.includes(day)) {
            cell.classList.add("pending-day");
        }

        const today = new Date();

        if (
            day === today.getDate() &&
            month === today.getMonth() &&
            year === today.getFullYear()
        ) {
            cell.classList.add("today");
        }

        cell.addEventListener("click", () => {

            if (approvedDays.includes(day)) {

                alert(
                    `Approved Volunteer Task\n\nDate: ${day} ${months[month]}`
                );

            } else if (pendingDays.includes(day)) {

                alert(
                    `Pending Volunteer Application\n\nDate: ${day} ${months[month]}`
                );

            } else {

                alert(
                    `No volunteer activity on ${day} ${months[month]}`
                );

            }

        });

        calendarDays.appendChild(cell);

    }

}

renderCalendar(currentDate);

const prevBtn = document.getElementById("prevMonth");
const nextBtn = document.getElementById("nextMonth");

if (prevBtn) {

    prevBtn.addEventListener("click", () => {

        currentDate.setMonth(currentDate.getMonth() - 1);

        renderCalendar(currentDate);

    });

}

if (nextBtn) {

    nextBtn.addEventListener("click", () => {

        currentDate.setMonth(currentDate.getMonth() + 1);

        renderCalendar(currentDate);

    });

}

// ---------- Apply Buttons ----------

document.querySelectorAll(".apply-btn").forEach(button => {

    button.addEventListener("click", () => {

        button.textContent = "Applied";
        button.disabled = true;
        button.style.opacity = ".7";

        alert("Application submitted successfully!");

    });

});

// ---------- Cancel Application ----------

document.querySelectorAll(".cancel-btn").forEach(button => {

    button.addEventListener("click", () => {

        if (!confirm("Cancel this application?")) {
            return;
        }

        const card = button.closest(".application-card");

        if (card) {
            card.remove();
        }

        alert("Application cancelled.");

    });

});

// ---------- Rejection Reason ----------

document.querySelectorAll(".reason-btn").forEach(button => {

    button.addEventListener("click", () => {

        alert(
            "Reason:\n\nAnother volunteer had already been assigned to this task."
        );

    });

});

// ---------- Inquiry Form ----------

const inquiryForm = document.getElementById("inquiryForm");

if (inquiryForm) {

    inquiryForm.addEventListener("submit", function (e) {

        e.preventDefault();

        const subject = document.getElementById("subject").value.trim();
        const category = document.getElementById("category").value;
        const message = document.getElementById("message").value.trim();

        if (!subject || !category || !message) {

            alert("Please complete all fields.");

            return;

        }

        alert("Inquiry sent successfully!");

        inquiryForm.reset();

    });

}
const task = volunteerTasks[day];

if (task) {

    // Colour the day
    if (task.status === "Approved") {
        cell.classList.add("approved-day");
    } else {
        cell.classList.add("pending-day");
    }

    // Make it look clickable
    cell.style.cursor = "pointer";

    // Only these dates open the popup
    cell.addEventListener("click", () => {

        document.getElementById("modalTitle").textContent = task.title;

        document.getElementById("modalDate").textContent =
            `${day} ${months[month]} ${year}`;

        document.getElementById("modalTime").textContent =
            task.time;

        document.getElementById("modalLocation").textContent =
            task.location;

        document.getElementById("modalStatus").textContent =
            task.status;

        document.getElementById("modalDescription").textContent =
            task.description;

        const status = document.getElementById("modalStatus");

        status.classList.remove("approved-text", "pending-text");

        if (task.status === "Approved") {
            status.classList.add("approved-text");
        } else {
            status.classList.add("pending-text");
        }

        document.getElementById("taskModal").style.display = "flex";

    });

} else {

    // Dates without tasks are not interactive
    cell.style.cursor = "default";

}

// =======================================
// MY APPLICATIONS PAGE
// =======================================

// ------------------------------
// REJECTION REASON MODAL
// ------------------------------

const reasonModal = document.getElementById("reasonModal");
const reasonButton = document.getElementById("viewReasonBtn");

if (reasonButton) {

    reasonButton.addEventListener("click", () => {

        reasonModal.style.display = "flex";

    });

}

// ------------------------------
// APPLICATION DETAILS MODAL
// ------------------------------

const detailsModal = document.getElementById("detailsModal");

document.querySelectorAll(".details-btn").forEach(button => {

    button.addEventListener("click", () => {

        const row = button.closest("tr");

        const task = row.cells[0].textContent;
        const date = row.cells[1].textContent;
        const location = row.cells[2].textContent;
        const status = row.cells[3].textContent.trim();

        document.getElementById("detailTask").textContent = task;
        document.getElementById("detailDate").textContent = date;
        document.getElementById("detailLocation").textContent = location;
        document.getElementById("detailStatus").textContent = status;

        detailsModal.style.display = "flex";

    });

});

// ------------------------------
// CANCEL APPLICATION
// ------------------------------

document.querySelectorAll(".cancel-btn").forEach(button => {

    button.addEventListener("click", () => {

        if (!confirm("Are you sure you want to cancel this application?")) {

            return;

        }

        button.closest("tr").remove();

        alert("Application cancelled successfully.");

    });

});

// ------------------------------
// CLOSE MODALS
// ------------------------------

document.querySelectorAll(".close-modal").forEach(closeBtn => {

    closeBtn.addEventListener("click", () => {

        closeBtn.closest(".modal").style.display = "none";

    });

});

window.addEventListener("click", (event) => {

    document.querySelectorAll(".modal").forEach(modal => {

        if (event.target === modal) {

            modal.style.display = "none";

        }

    });

});

/* ==========================================
   VOLUNTEER INQUIRIES
========================================== */

const volunteer_inquiryForm = document.getElementById("inquiryForm");

if (volunteer_inquiryForm) {

    volunteer_inquiryForm.addEventListener("submit", function (e) {

        e.preventDefault();

        const subject = document.getElementById("subject").value.trim();
        const category = document.getElementById("category").value;
        const message = document.getElementById("message").value.trim();

        if (subject === "" || message === "") {

            alert("Please complete all fields.");

            return;

        }

        const historySection =
            document.querySelector(".dash-block:last-of-type");

        const card = document.createElement("div");

        card.className = "inquiry-card";

        const today = new Date();

        const date = today.toLocaleDateString("en-GB", {

            day: "numeric",
            month: "long",
            year: "numeric"

        });

        card.innerHTML = `

            <div class="inquiry-header">

                <div class="user-info">

                    <span class="material-symbols-outlined">

                        account_circle

                    </span>

                    <div>

                        <h4>You</h4>

                        <span class="inquiry-date">

                            ${date}

                        </span>

                    </div>

                </div>

            </div>

            <div class="message-box">

                <div class="message-subject">

                    ${subject}

                </div>

                <div class="message-category">

                    ${category}

                </div>

                <p>

                    ${message}

                </p>

            </div>

            <div class="pending-reply">

                <span class="material-symbols-outlined">

                    schedule

                </span>

                Awaiting a response from PawCare Management...

            </div>

        `;

        historySection.appendChild(card);

        inquiryForm.reset();

        alert("Your inquiry has been submitted successfully.");

    });

}

/* ==========================================
   MARK REPLY AS READ
========================================== */

document.querySelectorAll(".mark-read-btn").forEach(button => {

    button.addEventListener("click", function () {

        const reply = this.closest(".management-reply");

        reply.classList.add("read");

        this.remove();

        const inquiryCard = reply.closest(".inquiry-card");

        const badge = inquiryCard.querySelector(".reply-badge");

        if (badge) {

            badge.remove();

        }

    });

});