inherit update-alternatives

ALTERNATIVE_LINK_NAME[python] = "${bindir}/python"

ALTERNATIVE:${PN}-core += "python"
ALTERNATIVE_TARGET[python] = "${bindir}/python3"
