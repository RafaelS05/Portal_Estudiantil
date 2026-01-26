document.addEventListener("DOMContentLoaded", () => {
  const requestMode = document.getElementById("requestMode");
  const resetMode = document.getElementById("resetMode");
  const btn = document.getElementById("ButtonToggleModoReset");
  if (!requestMode || !resetMode || !btn) return;

  btn.addEventListener("click", () => {
    requestMode.classList.toggle("d-none");
    resetMode.classList.toggle("d-none");
  });
});
