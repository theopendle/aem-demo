(function ($) {

    const ID_EDIT_BUTTON = "#editEntityButton"

    $(document).on("foundation-contentloaded", () => {

        const editButton = document.querySelector(ID_EDIT_BUTTON)
        if (!editButton) {
            console.error(`No button found with ID <${ID_EDIT_BUTTON}>`)
            return
        }

        // Open edit screen when user clicks edit button
        editButton.addEventListener("click", () => {
    
            const selectedItem = $(".foundation-selections-item")
            if (!selectedItem) {
                console.error("Delete was triggered but no item is selected")
                return
            }

            window.location = `/mnt/overlay/demo/consoles/genericentity/content/edit.html${selectedItem.data("path")}`
        })
    })

})(Granite.$)