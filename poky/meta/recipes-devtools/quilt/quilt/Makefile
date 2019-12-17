PATH :=         $(CURDIR)/bin:$(CURDIR)/compat:$(PATH)
QUILT_DIR :=    $(CURDIR)/quilt
QUILTRC :=      $(CURDIR)/test/test.quiltrc
export QUILT_DIR QUILTRC
CHECK_ENV := P=patches/; _P=../patches/; export P _P;
CHECK_ENV += QUILT_PC=.pc; export QUILT_PC
-include test/.depend

check-% : test/%.test
	@LANG=C; LC_ALL=C; \
	export LANG LC_ALL; \
	$(CHECK_ENV); \
	cd $(<D); \
	./run -q $(<F)
