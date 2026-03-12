SUMMARY = "Perfetto - System profiling, app tracing and trace analysis."
HOMEPAGE = "https://github.com/google/perfetto"

# Add only patches here which are used by both perfetto and libperfetto
SRC_URI = "git://github.com/google/perfetto.git;protocol=https;name=perfetto;nobranch=1 \
	"

SRCREV_perfetto = "7a9a6a0587348bffd1796b66a1da33cc1ea421d8"
SRCREV_FORMAT = "perfetto"
PV = "51.2"
