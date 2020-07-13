FILESEXTRAPATHS_prepend_nicole := "${THISDIR}/${PN}:"
SRC_URI_append_nicole = " \
    file://bootmailbox.override.yml \
    file://time-sync-method.override.yml \
"
