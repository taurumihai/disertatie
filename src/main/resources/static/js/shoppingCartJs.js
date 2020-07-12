function hideBillingForm() {

    if (document.getElementById('sameAddressCheckBox').checked) {
        document.getElementById('billingAddressForm').style.visibility ="hidden";
    } else {
        document.getElementById('billingAddressForm').style.visibility ="visible";
    }
}

function validateZipCode(event, value, maxLength) {

    if (value != undefined && value.toString().length >= maxLength) {
        event.preventDefault();
    }
}