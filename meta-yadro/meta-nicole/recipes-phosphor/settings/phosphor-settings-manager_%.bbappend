FILESEXTRAPATHS:prepend:nicole := "${THISDIR}/${PN}:"
SRC_URI:append:nicole = " \
    file://bootmailbox.override.yml \
    file://time-sync-method.override.yml \
"
