(function () {
    const WIREFRAME_CLASS = "wireframe";
    const wireframeButton = document.getElementById("wireframe-button");

    function toggleWireframeClass() {
        const overlayRoot = document.querySelector(".cq-Overlay--container");
        if (overlayRoot.classList.contains(WIREFRAME_CLASS)) {
            overlayRoot.classList.remove(WIREFRAME_CLASS);
        } else {
            overlayRoot.classList.add(WIREFRAME_CLASS);
        }
    }

    wireframeButton.addEventListener("click", () => {
        toggleWireframeClass();
    });
})();


