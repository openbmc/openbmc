FILESEXTRAPATHS:prepend:nicole := "${THISDIR}/${BPN}:"
SRC_URI:append:nicole = " \
    file://bootmailbox.override.yml \
    file://time-sync-method.override.yml \
"
