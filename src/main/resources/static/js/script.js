const dropArea = document.querySelector(".upload-box");
const fileInput = document.getElementById("fileInput");
const uploadText = document.getElementById("uploadText");
const separatorInput = document.getElementById("separatorInput");
const hints = document.querySelectorAll(".separator-hints span");
const convertBtn = document.getElementById("convertBtn");
const fileTypeSelect = document.querySelector("select[name='type']");
let formSubmitting = false;
const form = document.querySelector("form");

/* ---------- Helper ---------- */

function isTxtFile(file) {
    return file && file.name.toLowerCase().endsWith(".txt");
}

function showError(message) {
    uploadText.innerHTML = "❌ " + message;
    uploadText.style.color = "#e74c3c";
    validateForm();
}

function showFile(name) {
    uploadText.innerHTML = "✅ " + name;
    uploadText.style.color = "#2ecc71";
    validateForm();
}

/* ---------- FORM VALIDATION ---------- */
function validateForm() {

    const hasFile = fileInput.files.length > 0;
    const isTxt = hasFile && isTxtFile(fileInput.files[0]);
    const hasSeparator = separatorInput.value.trim().length > 0;
    const hasType = fileTypeSelect.value === "csv" || fileTypeSelect.value === "excel";

    convertBtn.disabled = !(hasFile && isTxt && hasSeparator && hasType);
}

/* ---------- AJAX Submit + Auto Download ---------- */
form.addEventListener("submit", async function (e) {
    e.preventDefault();

    if (formSubmitting) return;

    validateForm();
    if (convertBtn.disabled) return;

    formSubmitting = true;
    convertBtn.disabled = true;
    convertBtn.innerHTML = "⏳ Converting...";
    convertBtn.classList.add("loading");

    try {
        const formData = new FormData(form);

        const response = await fetch("/convert", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error("Conversion failed");
        }

        // Get filename from header
        let filename = "converted-file";
        const disposition = response.headers.get("Content-Disposition");
        if (disposition && disposition.includes("filename=")) {
            filename = disposition.split("filename=")[1].replace(/"/g, "");
        }

        // Convert response to blob
        const blob = await response.blob();

        // Create download
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);

        // Reset form after success
        form.reset();
        uploadText.innerHTML = `
            <span class="icon">📄</span>
            <p>Drag & Drop TXT file</p>
            <small>or click to browse</small>
        `;
        uploadText.style.color = "";
        hints.forEach(h => h.classList.remove("active"));
    } catch (err) {
        showError("Conversion failed. Please try again.");
    }

    // Unlock UI
    formSubmitting = false;
    convertBtn.innerHTML = "Convert File";
    convertBtn.classList.remove("loading");
    validateForm();
});

/* ---------- Prevent browser opening file ---------- */
["dragenter", "dragover", "dragleave", "drop"].forEach(eventName => {
    dropArea.addEventListener(eventName, e => e.preventDefault());
    document.body.addEventListener(eventName, e => e.preventDefault());
});

/* ---------- Highlight box ---------- */
["dragenter", "dragover"].forEach(eventName => {
    dropArea.addEventListener(eventName, () => dropArea.classList.add("dragover"));
});

["dragleave", "drop"].forEach(eventName => {
    dropArea.addEventListener(eventName, () => dropArea.classList.remove("dragover"));
});

/* ---------- DROP FILE ---------- */
dropArea.addEventListener("drop", e => {
    const files = e.dataTransfer.files;

    if (files.length === 0) return;

    if (!isTxtFile(files[0])) {
        fileInput.value = "";
        showError("Only .txt files are allowed");
        return;
    }

    fileInput.files = files;
    showFile(files[0].name);
});

/* ---------- Manual Select ---------- */
fileInput.addEventListener("change", () => {

    if (fileInput.files.length === 0) {
        validateForm();
        return;
    }

    if (!isTxtFile(fileInput.files[0])) {
        fileInput.value = "";
        showError("Only .txt files are allowed");
        return;
    }

    showFile(fileInput.files[0].name);
});

/* ---------- Separator Quick Select ---------- */
hints.forEach(hint => {
    hint.addEventListener("click", () => {

        hints.forEach(h => h.classList.remove("active"));
        hint.classList.add("active");

        let val = hint.getAttribute("data-value");
        if (val === "\\t") val = "\t";

        separatorInput.value = val;
        separatorInput.focus();
        validateForm();
    });
});

/* ---------- Listeners ---------- */
separatorInput.addEventListener("input", validateForm);
fileTypeSelect.addEventListener("change", validateForm);

/* ---------- Initial State ---------- */
validateForm();