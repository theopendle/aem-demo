import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators.js';

interface ITableItem {
    values: string[];
}

@customElement('demo-finder-ui5')
export class FinderUi5 extends LitElement {

    DATA: ITableItem[] = [
        { values: ["Handling of client data", "Business relationships", "3.21", new Date().toString(), "JSSH Group"] },
        { values: ["Securities trading", "Trading", "6.23", new Date().toString(), "JSSH Group"] },
        { values: ["Cash service", "Logistics", "1.03", new Date().toString(), "BJSS CH"] },
    ]

    private _renderRows() {
        return this.DATA
            .map(item => item.values.map(value => html`<ui5-table-cell>${value}</ui5-table-cell>`))
            .map(cells => html`<ui5-table-row>${cells}</ui5-table-row>`)
    }

    render() {
        return html`
        <form>
            <ui5-label class="samples-big-margin-right" for="myInput" required show-colon>Name</ui5-label>
            <ui5-input id="myInput" placeholder="Enter your Name" required></ui5-input>
        </form>
        
        <ui5-table>
            <!-- Columns -->
            <ui5-table-column slot="columns">Name</ui5-table-column>
            <ui5-table-column slot="columns">Type</ui5-table-column>
            <ui5-table-column slot="columns">Area</ui5-table-column>
            <ui5-table-column slot="columns">Number</ui5-table-column>
            <ui5-table-column slot="columns">Valid from</ui5-table-column>
        
            <!-- Rows -->
            ${this._renderRows()}
        
        </ui5-table>
    `;
    }
}
