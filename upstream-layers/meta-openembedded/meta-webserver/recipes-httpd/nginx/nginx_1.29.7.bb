require nginx.inc

SRC_URI:append = " \
    file://CVE-2026-40701.patch \
    file://CVE-2026-42934.patch \
    file://CVE-2026-42946_p1.patch \
    file://CVE-2026-42946_p2.patch \
    file://CVE-2026-42945.patch \
    file://CVE-2026-9256.patch \
    file://CVE-2026-42055.patch \
    file://CVE-2026-48142.patch \
    file://CVE-2026-42533_p1.patch \
    file://CVE-2026-42533_p2.patch \
    file://CVE-2026-42533_p3.patch \
    file://CVE-2026-42533_p4.patch \
    file://CVE-2026-42533_p5.patch \
    file://CVE-2026-56434.patch \
"

# 1.28.x branch is the current stable branch, the recommended default
# 1.29.x is the current mainline branches containing all new features
DEFAULT_PREFERENCE = "-1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=79da1c70d587d3a199af9255ad393f99"

SRC_URI[sha256sum] = "673f8fb8c0961c44fbd9410d6161831453609b44063d3f2948253fc2b5692139"

CVE_STATUS[CVE-2026-40460] = "${@ 'unpatched' if '--with-http_v3_module' in ((d.getVar('EXTRA_OECONF') or '').split() + (d.getVar('PACKAGECONFIG_CONFARGS') or '').split()) else 'not-applicable-config: ngx_http_v3_module is not enabled'}"
CVE_STATUS[CVE-2026-60005] = "${@ 'unpatched' if '--with-http_slice_module' in ( (d.getVar('EXTRA_OECONF') or '').split() + (d.getVar('PACKAGECONFIG_CONFARGS') or '').split()) else 'not-applicable-config: ngx_http_slice_module is not enabled' }"
