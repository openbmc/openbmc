inherit update-alternatives

ALTERNATIVE_${PN}-core += "python"
ALTERNATIVE_LINK_NAME[python] = "${bindir}/python"
ALTERNATIVE_TARGET[python] = "${bindir}/python3"
