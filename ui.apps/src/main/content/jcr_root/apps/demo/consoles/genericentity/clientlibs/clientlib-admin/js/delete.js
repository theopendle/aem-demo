(function ($, Coral) {

    const ID_DELETE_BUTTON = "#deleteEntityButton"
    const ID_DELETE_DIALOG = "#deleteEntityDialog"
    const DN_PATH = "path"

    function createConfirmButton(dialog) {
        const button = new Coral.Button().set({
            variant: "primary",
            innerText: Granite.I18n.get("Yes")
        })

        button.addEventListener("click", () => {
            const selectedItem = $(".foundation-selections-item")
            const switcherItem = $(".foundation-mode-switcher-item")

            // Send delete request when user confirms
            $.ajax({
                url: selectedItem.data("path"),
                type: "POST",
                data: {
                    ":operation": "delete"
                },
                success: function () {
                    selectedItem.remove()
                    closeDialog(dialog)
                    if (switcherItem) {
                        switcherItem.removeClass('foundation-mode-switcher-item-active')
                    }
                },
                error: function (xmlhttprequest, textStatus, message) {
                    closeDialog(dialog)
                    ui.notify(Granite.I18n.get("Error"), Granite.I18n.get("An error occurred while deleting the entity: <b>{0}</b>.", message), "error")
                }
            })
        })

        dialog.footer.appendChild(button)
        return button
    }

    function createDeleteDialog() {
        const dialog = new Coral.Dialog().set({
            variant: "warning",
            id: ID_DELETE_DIALOG,
            header: {
                innerHTML: Granite.I18n.get("Delete Entity")
            },
            content: {},
            footer: {
                innerHTML: `
                <button is='coral-button' variant='default' coral-close>
                    ${Granite.I18n.get("No")}
                </button>`
            }
        })

        createConfirmButton(dialog)

        document.body.appendChild(dialog)
        return dialog
    }

    function openDialog(dialog, selectedItem) {
        dialog.set({
            content: {
                innerHTML: Granite.I18n.get("Are you sure you want to delete entity <b>{0}</b>?", selectedItem.data("id"))
            }
        })
        dialog.show()
    }

    function closeDialog(dialog) {
        dialog.hide()
        dialog.set({
            content: {}
        })
    }

    $(document).on("foundation-contentloaded", () => {

        const deleteButton = document.querySelector(ID_DELETE_BUTTON)
        if (!deleteButton) {
            console.error(`No delete button found with ID <${ID_DELETE_BUTTON}>`)
            return
        }

        const dialog = createDeleteDialog()

        // Open dialog when user clicks on delete button
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

            openDialog(dialog, selectedItem)
        })


    })

})(Granite.$, Coral)