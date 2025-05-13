let flashcards = [];
let editingIndex = null;

const container = document.getElementById("card-container");
const modal = document.getElementById("modal");
const questionInput = document.getElementById("question");
const answerInput = document.getElementById("answer");

document.getElementById("add-card").onclick = () => {
  editingIndex = null;
  questionInput.value = "";
  answerInput.value = "";
  modal.classList.remove("hidden");
};

document.getElementById("cancel").onclick = () => {
  modal.classList.add("hidden");
};

document.getElementById("save-card").onclick = () => {
  const q = questionInput.value.trim();
  const a = answerInput.value.trim();
  if (!q || !a) return alert("Both fields are required!");
  const card = { question: q, answer: a };
  if (editingIndex !== null) flashcards[editingIndex] = card;
  else flashcards.push(card);
  modal.classList.add("hidden");
  renderCards();
};

function renderCards(cards = flashcards) {
  container.innerHTML = "";
  cards.forEach((card, i) => {
    const div = document.createElement("div");
    div.className = "card";
    div.innerHTML = `
      <div class="content">${card.question}</div>
      <div class="actions">
        <button onclick="editCard(${i})">✏️</button>
        <button onclick="deleteCard(${i})">🗑️</button>
      </div>
    `;
    div.onclick = (e) => {
      if (e.target.tagName === "BUTTON") return;
      const content = div.querySelector(".content");
      content.textContent =
        content.textContent === card.question ? card.answer : card.question;
    };
    container.appendChild(div);
  });
}

function editCard(i) {
  editingIndex = i;
  questionInput.value = flashcards[i].question;
  answerInput.value = flashcards[i].answer;
  modal.classList.remove("hidden");
}

function deleteCard(i) {
  if (confirm("Delete this card?")) {
    flashcards.splice(i, 1);
    renderCards();
  }
}

document.getElementById("search").addEventListener("input", (e) => {
  const term = e.target.value.toLowerCase();
  const filtered = flashcards.filter(
    (card) =>
      card.question.toLowerCase().includes(term) ||
      card.answer.toLowerCase().includes(term)
  );
  renderCards(filtered);
});

document.getElementById("shuffle").onclick = () => {
  flashcards = flashcards.sort(() => Math.random() - 0.5);
  renderCards();
};

document.getElementById("toggle-theme").onclick = () => {
  const current = document.documentElement.getAttribute("data-theme");
  const next = current === "dark" ? "light" : "dark";
  document.documentElement.setAttribute("data-theme", next);
};

document.getElementById("export").onclick = () => {
  const blob = new Blob([JSON.stringify(flashcards)], { type: "application/json" });
  const link = document.createElement("a");
  link.href = URL.createObjectURL(blob);
  link.download = "flashcards.json";
  link.click();
};

document.getElementById("import-json").addEventListener("change", function () {
  const file = this.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = function (e) {
    try {
      const data = JSON.parse(e.target.result);
      if (Array.isArray(data)) {
        flashcards = data;
        renderCards();
      } else {
        alert("Invalid JSON format.");
      }
    } catch {
      alert("Could not parse file.");
    }
  };
  reader.readAsText(file);
});
