DESCRIPTION = "Image with Phosphor, an OpenBMC framework."

inherit obmc-mellanox-image extrausers

IMAGE_INSTALL += "\
	sudo \
	openipmi \
"
EXTRA_USERS_PARAMS = "useradd -G root admin; \
			usermod -p '\$1\$RoxpjHVR\$YbwQZSupk2iNIIJVRng9w0' admin; \
			usermod -aG sudo admin; \
			usermod -p '\$1\$WpCI7Odv\$AMVM5qFXLmImzE8eqQv6y0' root; \
			"

