(function($){

    const AN_ACTION = "action"
    const ID_CREATE_FORM = "#createEntityForm"

    $(document).on("foundation-contentloaded", () => {

        const formElement = document.querySelector(ID_CREATE_FORM)
        if(!formElement) {
            console.error(`No form found with ID <${ID_CREATE_FORM}>`)
            return
        }

        const rootActionPath = formElement.getAttribute(AN_ACTION)
        if(!rootActionPath) {
            console.error(`No <${AN_ACTION}> found on form with ID <${ID_CREATE_FORM}>`)
            return
        }

        const nameFieldElement = formElement.querySelector("[name='./name']")

        nameFieldElement.addEventListener("change", event => {
            const name = event.target.value
            formElement.setAttribute(AN_ACTION, rootActionPath + "/" + name)
        })

    })

})(Granite.$)