import {LitElement, html} from 'lit';
import {customElement} from 'lit/decorators.js';

@customElement("demo-finder")
export class DemoFinder extends LitElement {

  render() {
    return html`
    <form>
        <slot name="search"></slot>
        <slot name="results"></slot>
    </form> 
    `;
  }
}
