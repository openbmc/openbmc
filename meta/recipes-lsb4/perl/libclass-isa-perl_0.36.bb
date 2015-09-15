SUMMARY = "Perl module for reporting the search path for a class's ISA tree"
DESCRIPTION = "Suppose you have a class (like Food::Fish::Fishstick) that is derived, \
via its @ISA, from one or more superclasses (as Food::Fish::Fishstick is from Food::Fish,\
Life::Fungus, and Chemicals), and some of those superclasses may themselves each be\
derived, via its @ISA, from one or more superclasses (as above).\
\
When, then, you call a method in that class ($fishstick->calories), Perl first searches\
there for that method, but if it's not there, it goes searching in its superclasses, and\
so on, in a depth-first (or maybe "height-first" is the word) search. In the above example,\
it'd first look in Food::Fish, then Food, then Matter, then Life::Fungus, then Life, then\
Chemicals.\
\
This library, Class::ISA, provides functions that return that list -- the list\
(in order) of names of classes Perl would search to find a method, with no duplicates."

HOMEPAGE = "http://search.cpan.org/dist/Class-ISA/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;beginline=107;endline=111;md5=6a5c6842a63cfe4dab1f66e2350e4d25"

SRC_URI = "http://search.cpan.org/CPAN/authors/id/S/SM/SMUELLER/Class-ISA-${PV}.tar.gz"

SRC_URI[md5sum] = "3a2ad203c8dc87d6c9de16215d00af47"
SRC_URI[sha256sum] = "8816f34e9a38e849a10df756030dccf9fe061a196c11ac3faafd7113c929b964"

S = "${WORKDIR}/Class-ISA-${PV}"

inherit cpan

BBCLASSEXTEND = "native"
