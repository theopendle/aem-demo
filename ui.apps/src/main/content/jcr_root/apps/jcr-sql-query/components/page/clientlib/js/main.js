(function () {
    const QUERY_FIELD_ID = "query-field";
    const RESULT_TABLE_ID = "result-table";
    const FEEDBACK_ALERT_CONTENT_ID = "feedback-alert-content";

    window.app = {

        executeQuery: () => {
            const queryField = document.getElementById(QUERY_FIELD_ID);
            if (!queryField) {
                console.error(`Cannot find query field by id '${QUERY_FIELD_ID}'`);
                return;
            }
            const query = encodeURIComponent(queryField.value);

            fetch(`/bin/query/sql2?q=${query}`)
                .then(response => {

                    const alertContent = document.getElementById(FEEDBACK_ALERT_CONTENT_ID);
                    if (!alertContent) {
                        console.error(`Cannot find feedback alert by id '${FEEDBACK_ALERT_CONTENT_ID}'`);
                    }

                    // If bad request, show feedback to user
                    if (response.status === 400) {
                        response.json().then(function (data) {
                            if (data.feedback) {
                                alertContent.innerText = data.feedback;
                            }

                            alertContent.parentElement.removeAttribute("hidden");
                        });
                        return;
                    }

                    // If unknown error code, log error
                    if (response.status !== 200) {
                        console.error('Error querying servlet. HTTP code: ' + response.status);
                        return;
                    }

                    // If response OK, show results in table
                    response.json().then(function (data) {
                        alertContent.parentElement.setAttribute("hidden", "true");

                        const table = document.getElementById(RESULT_TABLE_ID);
                        if (!table) {
                            console.error(`Cannot find result table by id '${RESULT_TABLE_ID}'`);
                        }
                    });


                })
                .catch(error => {
                    console.log('Fetch error: ', error);
                });
        }

    };
})();
