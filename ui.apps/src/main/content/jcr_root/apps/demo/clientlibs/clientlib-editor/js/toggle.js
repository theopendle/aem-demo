(function (document, $) {
    "use strict";

    var TOGGLE_ATTRIBUTE_PREFIX = "data-toggle-";
    var MASTER_ATTRIBUTE_SUFFIX = "_master";
    var SLAVE_ATTRIBUTE_SUFFIX = "_slave";
    var DIALOG_CONTENT_SELECTOR = ".cq-dialog-content";


    /**
     * Build the master and slave attribute names from the toggle name.
     * @param {string} toggleName
     */
    function getAttributes(toggleName) {
        return {
            master: TOGGLE_ATTRIBUTE_PREFIX + toggleName + MASTER_ATTRIBUTE_SUFFIX,
            slave: TOGGLE_ATTRIBUTE_PREFIX + toggleName + SLAVE_ATTRIBUTE_SUFFIX
        }
    }

    /**
     * Builds the master and slave selectors from the toggle name.
     * @param {string} toggleName
     */
    function getSelectors(toggleName) {
        var attributes = getAttributes(toggleName);
        return {
            master: "[" + attributes.master + "]",
            slave: "[" + attributes.slave + "]"
        }
    }

    var toggles = [
        {
            name: "checkbox",
            updateFunction: function (master, $slaves) {
                var isChecked = master[0].hasAttribute("checked");
                $slaves.each(function () {
                    $(this).prop("disabled", isChecked.toString() === $(this).attr(getAttributes("checkbox").slave));
                })
            }
        },
        {
            name: "textfield",
            updateFunction: function (master, $slaves) {
                var show = master[0].value === "show secret";
                $slaves.each(function () {
                    $(this).parent().prop("hidden", !show);
                })
            }
        }
    ];

    toggles.forEach(function (toggle) {

        var selectors = getSelectors(toggle.name);

        // When the dialog is loaded, init all slaves
        $(document).on("foundation-contentloaded", function (e) {

            // Find the dialog
            var $dialog = $(e.target);
            if ($dialog && $dialog.length === 1) {

                // Find the toggel master
                var $master = $dialog.find(selectors.master);
                if ($master) {
                    if ($master.length !== 1) {
                        console.error($master.length + " masters for toggle <" + toggle + ">");
                        return;
                    }

                    // Update slaves
                    var $slaves = $dialog.find(selectors.slave);
                    toggle.updateFunction($master, $slaves);
                }
            }
        });

        // When a value is changed, trigger update
        $(document).on("change", function (e) {

            // Find the master which was updated
            var $master = $(e.target);
            var $dialog = $master.parents(DIALOG_CONTENT_SELECTOR);
            if ($master && $master.length === 1 && $master.is(selectors.master)) {

                // Update slaves
                var $slaves = $dialog.find(selectors.slave);
                toggle.updateFunction($master, $slaves);
            }
        });
    });

})(document, Granite.$);