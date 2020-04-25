(function () {
    const QUERY_FIELD_ID = "query-field";
    const RESULT_CONTAINER_ID = "result-container";
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
                        return;
                    }

                    const tableContainer = document.getElementById(RESULT_CONTAINER_ID);
                    if (!tableContainer) {
                        console.error(`Cannot find result table by id '${RESULT_CONTAINER_ID}'`);
                        return;
                    }

                    // If bad request, show feedback to user
                    if (response.status !== 200) {
                        response.json().then(function (data) {
                            if (data.feedback) {
                                alertContent.innerText = data.feedback;
                            }

                            alertContent.parentElement.removeAttribute("hidden");
                            tableContainer.innerHTML = '';
                        });
                        return;
                    }

                    // If response OK, show results in table
                    response.json().then(function (data) {
                        alertContent.parentElement.setAttribute("hidden", "true");

                        tableContainer.innerHTML = `
                            <table id="result-table" 
                                   class="table-example"
                                   is="coral-table"
                                   selectable>
                                 <thead is="coral-table-head">
                                        <tr is="coral-table-row">
                                            ${data.headers.map(header => `
                                            <th is="coral-table-headercell">${header}</th>
                                            `).join('')}
                                        </tr>
                                </thead>
                                <tbody is="coral-table-body">
                                ${data.rows.map(row => `
                                    <tr is="coral-table-row">
                                        ${row.values.map(value => `<td is="coral-table-cell">${value}</td>`).join('')}
                                    </tr>
                                `).join('')}
                                </tbody>
                            </table>`;
                    });
                })
                .catch(error => {
                    console.log('Fetch error: ', error);
                });
        }

    };
})();
