
lib_LTLIBRARIES = libbz2.la
libbz2_la_LDFLAGS = -version-info 1:6:0

libbz2_la_SOURCES = blocksort.c  \
                    huffman.c    \
                    crctable.c   \
                    randtable.c  \
                    compress.c   \
                    decompress.c \
                    bzlib.c

bin_PROGRAMS = bzip2 bzip2recover

bzip2_SOURCES = bzip2.c
bzip2_LDADD = libbz2.la
bzip2_DEPENDENCIES = libbz2.la

include_HEADERS = bzlib.h

bzip2recover_SOURCES = bzip2recover.c
bzip2recover_LDADD = libbz2.la
bzip2recover_DEPENDENCIES = libbz2.la

bin_SCRIPTS = bzgrep bzmore bzdiff

man_MANS = bzip2.1 bzgrep.1 bzmore.1 bzdiff.1
EXTRA_DIST = $(man_MANS)

runtest:
	./bzip2 -1  < sample1.ref > sample1.rb2
	./bzip2 -2  < sample2.ref > sample2.rb2
	./bzip2 -3  < sample3.ref > sample3.rb2
	./bzip2 -d  < sample1.bz2 > sample1.tst
	./bzip2 -d  < sample2.bz2 > sample2.tst
	./bzip2 -ds < sample3.bz2 > sample3.tst
	@if cmp sample1.bz2 sample1.rb2; then echo "PASS: sample1 compress";\
	else echo "FAIL: sample1 compress"; fi
	@if cmp sample2.bz2 sample2.rb2; then echo "PASS: sample2 compress";\
	else echo "FAIL: sample2 compress"; fi
	@if cmp sample3.bz2 sample3.rb2; then echo "PASS: sample3 compress";\
	else echo "FAIL: sample3 compress"; fi
	@if cmp sample1.tst sample1.ref; then echo "PASS: sample1 decompress";\
	else echo "FAIL: sample1 decompress"; fi
	@if cmp sample2.tst sample2.ref; then echo "PASS: sample2 decompress";\
	else echo "FAIL: sample2 decompress"; fi
	@if cmp sample3.tst sample3.ref; then echo "PASS: sample3 decompress";\
	else echo "FAIL: sample3 decompress"; fi

install-ptest:
	sed  -n '/^runtest:/,/^install-ptest:/{/^install-ptest:/!p}' \
           $(srcdir)/Makefile.am      > $(DESTDIR)/Makefile
	cp $(srcdir)/sample1.ref	$(DESTDIR)/
	cp $(srcdir)/sample2.ref	$(DESTDIR)/
	cp $(srcdir)/sample3.ref	$(DESTDIR)/
	cp $(srcdir)/sample1.bz2	$(DESTDIR)/
	cp $(srcdir)/sample2.bz2	$(DESTDIR)/
	cp $(srcdir)/sample3.bz2	$(DESTDIR)/
	ln -s $(bindir)/bzip2		$(DESTDIR)/bzip2

install-exec-hook:
	ln -s $(bindir)/bzip2$(EXEEXT) $(DESTDIR)$(bindir)/bunzip2$(EXEEXT)
	ln -s $(bindir)/bzip2$(EXEEXT) $(DESTDIR)$(bindir)/bzcat$(EXEEXT)
	ln -s $(bindir)/bzgrep$(EXEEXT) $(DESTDIR)$(bindir)/bzegrep$(EXEEXT)
	ln -s $(bindir)/bzgrep$(EXEEXT) $(DESTDIR)$(bindir)/bzfgrep$(EXEEXT)
	ln -s $(bindir)/bzmore$(EXEEXT) $(DESTDIR)$(bindir)/bzless$(EXEEXT)
	ln -s $(bindir)/bzdiff$(EXEEXT) $(DESTDIR)$(bindir)/bzcmp$(EXEEXT)

install-data-hook:
	echo ".so man1/bzgrep.1" > $(DESTDIR)$(mandir)/man1/bzegrep.1
	echo ".so man1/bzgrep.1" > $(DESTDIR)$(mandir)/man1/bzfgrep.1
	echo ".so man1/bzmore.1" > $(DESTDIR)$(mandir)/man1/bzless.1
	echo ".so man1/bzdiff.1" > $(DESTDIR)$(mandir)/man1/bzcmp.1
