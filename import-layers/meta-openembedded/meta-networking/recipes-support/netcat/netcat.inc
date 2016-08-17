SUMMARY = "GNU Netcat"
HOMEPAGE = "http://netcat.sourceforge.net"
DESCRIPTION = "A simple Unix utility which reads and writes data across network \
connections using TCP or UDP protocol.  It is designed to be a reliable \
'back-end' tool that can be used directly or easily driven by other \
programs and scripts. At the same time it is a feature-rich network \
debugging and exploration tool, since it can create almost any kind of \
connection you would need and has several interesting built-in \
capabilities."

SECTION = "net"
inherit update-alternatives gettext

ALTERNATIVE_${PN} = "nc"

