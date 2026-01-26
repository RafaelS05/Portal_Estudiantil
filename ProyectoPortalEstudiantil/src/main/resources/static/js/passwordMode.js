document.addEventListener("DOMContentLoaded", () => {
  const requestMode = document.getElementById("requestMode");
  const resetMode = document.getElementById("resetMode");
  const btn = document.getElementById("ButtonToggleModoReset");
  const tokenInput = document.getElementById("tokenInput");

  if (!requestMode || !resetMode || !btn) return;

  function hasToken() {
    const t = tokenInput ? (tokenInput.value || "").trim() : "";
    return t.length > 0;
  }

  btn.addEventListener("click", () => {
    // Si intenta pasar a reset y no hay token -> bloquea
    const goingToReset = !resetMode.classList.contains("d-none");

    // La lógica anterior “togglea” a ciegas; mejor control:
    if (resetMode.classList.contains("d-none")) {
      // quiere mostrar reset
      if (!hasToken()) {
        alert("Debes ingresar desde el enlace de recuperación (token). Primero solicita el enlace.");
        return;
      }
      requestMode.classList.add("d-none");
      resetMode.classList.remove("d-none");
      return;
    }

    // si está en reset, vuelve a request
    resetMode.classList.add("d-none");
    requestMode.classList.remove("d-none");
  });

  // Si el server ya te mandó modo=reset, respétalo,
  // pero igual exige token
  if (!resetMode.classList.contains("d-none") && !hasToken()) {
    resetMode.classList.add("d-none");
    requestMode.classList.remove("d-none");
  }
});
