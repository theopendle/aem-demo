(function ($) {

    const ID_DELETE_BUTTON = "#deleteEntityButton"
    const ID_DELETE_DIALOG = "#deleteEntityDialog"
    const DN_PATH = "path"

    $(document).on("foundation-contentloaded", () => {

        const deleteButton = document.querySelector(ID_DELETE_BUTTON)
        if(!deleteButton) {
            console.error(`No delete button found with ID <${ID_DELETE_BUTTON}>`)
            return
        }

        deleteButton.addEventListener("click", () => {

            const selectedItem = $(".foundation-selections-item")
            if (!selectedItem) {
                console.error("Delete was triggered but no item is selected")
                return
            }

            const selectedItemPath = $(".foundation-selections-item").data(DN_PATH)
            if (!selectedItemPath) {
                console.error(`Delete was triggered for item <${selectedItem}> but no path was found using attribute <${DN_PATH}>`)
                return
            }

            const dialog = document.querySelector(ID_DELETE_DIALOG)
            if (!dialog) {
                console.error(`Could not find delete dialog using ID <${ID_DELETE_DIALOG}>`)
                return
            }

            const switcherItem = $(".foundation-mode-switcher-item")
            $.ajax({
                url: selectedItemPath,
                type: "POST",
                data: {
                    ":operation": "delete"
                },
                success: function () {
                    selectedItem.remove()
                    if (dialog) {
                        dialog.hide()
                    }
                    if (switcherItem) {
                        switcherItem.removeClass('foundation-mode-switcher-item-active')
                    }
                    window.location.refresh
                },
                error: function (xmlhttprequest, textStatus, message) {
                    if (dialog) {
                        dialog.hide()
                    }
                    ui.notify(Granite.I18n.get("Error"), Granite.I18n.get("An error occurred while deleting the entry: {0}.", message, "0 is the error message"), "error")
                }
            })
        })
    })

})(Granite.$)