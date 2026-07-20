// Swap views based on user choice
  function handleRoleSelection(role) {
    // Save chosen state to structural field
    document.getElementById('roleInput').value = role;
    
    // UI Elements configuration toggles
    const roleStep = document.getElementById('step-role-selection');
    const formStep = document.getElementById('regForm');
    const subText = document.getElementById('formSubText');
    const volunteerSection = document.getElementById('volunteerField');
    const ownerSection = document.getElementById('ownerField');

    // Hide initial selection layer and show registration fields container
    roleStep.style.display = 'none';
    formStep.style.display = 'block';

    // Show specific fields based on profile context
    if (role === 'volunteer') {
      subText.textContent = 'Complete your profile details to join our rescue network team.';
      volunteerSection.classList.add('show');
      ownerSection.style.display = 'none';
    } else {
      subText.textContent = 'Register your personal details to begin booking premium pet care.';
      volunteerSection.classList.remove('show');
      ownerSection.style.display = 'block';
    }
  }

  // Allow clicking back to reset options
  function showRoleSelection(event) {
    event.preventDefault();
    document.getElementById('step-role-selection').style.display = 'block';
    document.getElementById('regForm').style.display = 'none';
    document.getElementById('formSubText').textContent = 'Join PawCare — pick your role to get started.';
  }

  function checkStrength(val) {
    const bar = document.getElementById('strengthBar');
    const txt = document.getElementById('strengthText');
    bar.className = 'strength-bar';
    if (val.length === 0) { txt.textContent = 'Use 8+ characters with numbers and symbols.'; return; }
    const strong = val.length >= 10 && /[0-9]/.test(val) && /[^a-zA-Z0-9]/.test(val);
    const medium = val.length >= 8 && (/[0-9]/.test(val) || /[^a-zA-Z0-9]/.test(val));
    if (strong) { bar.classList.add('strong'); txt.textContent = 'Strong password ✓'; txt.style.color = '#2E7D52'; }
    else if (medium) { bar.classList.add('medium'); txt.textContent = 'Medium — add a symbol to strengthen.'; txt.style.color = '#C8883A'; }
    else { bar.classList.add('weak'); txt.textContent = 'Too short — use at least 8 characters.'; txt.style.color = '#C0392B'; }
  }

  function selectType(card, role) {
    document.querySelectorAll('.type-card').forEach(c => c.classList.remove('selected'));
    card.classList.add('selected');
    document.getElementById('roleInput').value = role;
    const vf = document.getElementById('volunteerField');
    vf.classList.toggle('show', role === 'volunteer');
  }

  function checkStrength(val) {
    const bar = document.getElementById('strengthBar');
    const txt = document.getElementById('strengthText');
    bar.className = 'strength-bar';
    if (val.length === 0) { txt.textContent = 'Use 8+ characters with numbers and symbols.'; return; }
    const strong = val.length >= 10 && /[0-9]/.test(val) && /[^a-zA-Z0-9]/.test(val);
    const medium = val.length >= 8 && (/[0-9]/.test(val) || /[^a-zA-Z0-9]/.test(val));
    if (strong) { bar.classList.add('strong'); txt.textContent = 'Strong password ✓'; txt.style.color = '#2E7D52'; }
    else if (medium) { bar.classList.add('medium'); txt.textContent = 'Medium — add a symbol to strengthen.'; txt.style.color = '#C8883A'; }
    else { bar.classList.add('weak'); txt.textContent = 'Too short — use at least 8 characters.'; txt.style.color = '#C0392B'; }
  }