
// Stylesheets
import "./main.scss";

// UI5
import '@ui5/webcomponents/dist/Input'
import '@ui5/webcomponents/dist/Table'
import '@ui5/webcomponents/dist/TableColumn'
import '@ui5/webcomponents/dist/TableRow'
import '@ui5/webcomponents/dist/TableCell'
import '@ui5/webcomponents/dist/Button'
import '@ui5/webcomponents-icons/dist/search'
import '@ui5/webcomponents-icons/dist/decline'
import { setTheme } from "@ui5/webcomponents-base/dist/config/Theme.js";
setTheme("sap_horizon_hcb");


// Shoelace
import '@shoelace-style/shoelace/dist/components/input/input'
import '@shoelace-style/shoelace/dist/components/icon/icon'
import '@shoelace-style/shoelace/dist/components/button/button'
import { setBasePath } from '@shoelace-style/shoelace/dist/utilities/base-path.js';
setBasePath('clientlib-site/shoelace');


// Typescript
import '../components/**/*.ts';
