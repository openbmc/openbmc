SUMMARY = "Perfetto - System profiling, app tracing and trace analysis."
HOMEPAGE = "https://github.com/google/perfetto"

# Add only patches here which are used by both perfetto and libperfetto
SRC_URI = "git://github.com/google/perfetto.git;protocol=https;name=perfetto;nobranch=1 \
	"

SRCREV_perfetto = "da1d152cff27890903d158fe96751de3aab883cc"
SRCREV_FORMAT = "perfetto"
PV = "57.2"
