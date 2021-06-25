**perl-cross** provides configure script, top-level Makefile
and some auxiliary files for [perl](http://www.perl.org),  
with the primary emphasis on cross-compiling the source.  

    # Get perl and perl-cross sources
    curl -L -O http://www.cpan.org/src/5.0/perl-5.24.1.tar.gz
    curl -L -O https://github.com/arsv/perl-cross/releases/download/1.1.3/perl-cross-1.1.3.tar.gz

    # Unpack perl-cross over perl, overwriting Makefile
    tar -zxf perl-5.24.1.tar.gz
    cd perl-5.24.1
    tar --strip-components=1 -zxf ../perl-cross-1.1.3.tar.gz

    # Proceed as usual with most autoconfed packages
    ./configure --target=arm-linux-gnueabi --prefix=/usr -Duseshrplib
    make -j4
    make DESTDIR=/path/to/staging/dir install

Unlike mainline Perl, this configure never runs any target executables,  
relying solely on compile/link tests and pre-defined hints.  
On the flip side, it is only meant to run on resonably sane modern unix systems.  

Check [project pages](http://arsv.github.io/perl-cross/) for more info.  
In particular, [configure usage](http://arsv.github.io/perl-cross/usage.html)
lists available configure options.

Perl-cross is a free software licensed under the same terms
as the original perl source.  
See LICENSE, Copying and Artistic files.
