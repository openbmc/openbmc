inherit setuptools3
require python-fail2ban.inc

RDEPENDS_${PN}-ptest = "python3-core python3-io python3-modules python3-fail2ban"
