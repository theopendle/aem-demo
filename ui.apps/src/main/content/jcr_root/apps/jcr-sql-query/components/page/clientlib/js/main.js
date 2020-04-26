(function () {
    const QUERY_FIELD_ID = "query-field";
    const RESULT_CONTAINER_ID = "result-container";
    const FEEDBACK_ALERT_CONTENT_ID = "feedback-alert-content";

    window.app = {

        executeQuery: () => {
            const start = performance.now();

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
                            alertContent.innerText = data.feedback || data.error;

                            alertContent.parentElement.removeAttribute("hidden");
                            tableContainer.setAttribute("hidden", "true");
                        });
                        return;
                    }

                    // If response OK, show results in table
                    response.json().then(function (data) {
                        alertContent.parentElement.setAttribute("hidden", "true");

                        tableContainer.innerHTML = `
                            <p>
                                <strong>${data.table.rows.length}</strong> rows in <strong>${data.executionTime}s</strong>
                            </p>
                            <table id="result-table" 
                                   class="table-example"
                                   is="coral-table"
                                   selectable>
                                 <thead is="coral-table-head">
                                        <tr is="coral-table-row">
                                            <th is="coral-table-headercell">N°</th>
                                            ${data.table.header.cells.map(header => `
                                            <th is="coral-table-headercell">${header}</th>
                                            `).join('')}
                                        </tr>
                                </thead>
                                <tbody is="coral-table-body">
                                ${data.table.rows.map((row, index) => `
                                    <tr is="coral-table-row">
                                        <td is="coral-table-cell">${index + 1}</td>
                                        ${row.cells.map(value => `<td is="coral-table-cell">${value}</td>`).join('')}
                                    </tr>
                                `).join('')}
                                </tbody>
                            </table>`;

                        tableContainer.removeAttribute("hidden");
                        tableContainer.scrollTo(0, 0)
                        console.log("Call to doSomething took " + (performance.now() - start) + " milliseconds.")
                    });
                })
                .catch(error => {
                    console.log('Fetch error: ', error);
                });
        },

        adjustTextArea: (element) => {
            element.style.height = "1px";
            element.style.height = (25 + element.scrollHeight) + "px";
        }
    };

    // Execute query with Ctrl+Enter
    document.addEventListener('keydown', function (event) {
        if (event.ctrlKey && event.key === 'Enter') {
            window.app.executeQuery();
        }
    });
})();
