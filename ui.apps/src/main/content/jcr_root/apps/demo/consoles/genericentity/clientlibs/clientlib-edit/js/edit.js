;(function($){

    $(document).on("foundation-contentloaded", () => {

        // Disable the name field
        const nameField = document.querySelector('[name="./name"]')
        nameField.setAttribute("disabled", "")

    })

})(Granite.$)