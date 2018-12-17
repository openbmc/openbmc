inherit pypi setuptools3 update-alternatives
require python-ndg-httpsclient.inc

ALTERNATIVE_${PN} = "ndg_httpclient"
ALTERNATIVE_LINK_NAME[ndg_httpclient] = "${bindir}/ndg_httpclient"
ALTERNATIVE_PRIORITY = "30"
