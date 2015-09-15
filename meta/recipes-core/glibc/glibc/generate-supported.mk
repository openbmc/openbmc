#!/usr/bin/make

include $(IN)

all:
	rm -f $(OUT)
	touch $(OUT)
	for locale in $(SUPPORTED-LOCALES); do \
		[ $$locale = true ] && continue; \
		echo $$locale | sed 's,/, ,' >> $(OUT); \
	done
