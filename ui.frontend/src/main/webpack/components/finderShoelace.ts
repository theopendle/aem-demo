import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators.js';

interface ITableItem {
    values: string[];
}

@customElement('demo-finder-sl')
export class FinderShoelace extends LitElement {

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
            <sl-input label="Text"></sl-input>
            <sl-input label="Number"></sl-input>

            <sl-button variant="default">
                <sl-icon slot="prefix" name="search"></sl-icon>
                Search
            </sl-button>
            <sl-button variant="default">
                <sl-icon slot="prefix" name="x-lg"></sl-icon>
                Reset
            </sl-button>
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
