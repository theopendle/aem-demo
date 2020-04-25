// helper utilities for notification, toast and waitTickers.
(function () {
  const Toast = customElements.get("coral-toast");
  let alertEl;
  function getAlertEl() {
    if (!alertEl) {
      alertEl = new Coral.Dialog().set({
        closable: "on"
      }).on("coral-overlay:close", function (e) {
        e.target.remove();
      });
    }
    return alertEl;
  }

  function insertBefore(el, target) {
    return target.insertAdjacentElement('beforebegin', el);
  }

  window.AdminUtil = {
    // show alert dialog
    alert: function (title, message, type) {
      const el = getAlertEl();
      el.variant = type || "default";
      el.header.textContent = title || "";
      el.content.innerHTML = message || "";
      document.body.appendChild(el);
      el.show();
    },
    // show a toast.
    toast: function (text, variant, dismissAfter) {
      const toast = new Toast().set({
        content: {
          textContent: text
        },
        variant: variant || "info",
        autoDismiss: dismissAfter || 5000
      });
      toast.show();
      toast.on("coral-overlay:close", () => {
        if (Toast._queue && Toast._queue.length === 0) {
          // empty queue, destroy elements, no need to keep them in dom
          document.querySelectorAll("coral-toast").forEach(toast => toast.remove());
        }
      });
    },
    // show a dialog with loading icon. returns an API to update parts of the dialog and remove it.
    waitTicker: function (title, message) {
      const el = new Coral.Dialog();
      el.backdrop = Coral.Dialog.backdrop.STATIC;
      el.header.textContent = title;
      el.content.innerHTML = message || "";
      document.body.appendChild(el);
      const wait = new Coral.Wait();
      wait.style.marginRight = "5px"; // make it look nicer.
      insertBefore(wait, el.header);
      el.show();

      return {
        updateHeader: function (header) {
          el.header.innerHTML = header;
        },
        updateMessage: function (message) {
          el.content.innerHTML = message;
        },
        clear: function () {
          el.hide();
          requestAnimationFrame(function () {
            el.remove();
          });
        }
      };
    },
  };
}) ();
