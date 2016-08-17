" Vim plugin file
" Purpose:	Create a template for new bb files
" Author:	Ricardo Salveti <rsalveti@gmail.com>
" Copyright:	Copyright (C) 2008 Ricardo Salveti <rsalveti@gmail.com>
"
" This file is licensed under the MIT license, see COPYING.MIT in
" this source distribution for the terms.
"
" Based on the gentoo-syntax package
"
" Will try to use git to find the user name and email

if &compatible || v:version < 600
    finish
endif

fun! <SID>GetUserName()
    let l:user_name = system("git config --get user.name")
    if v:shell_error
        return "Unknown User"
    else
        return substitute(l:user_name, "\n", "", "")
endfun

fun! <SID>GetUserEmail()
    let l:user_email = system("git config --get user.email")
    if v:shell_error
        return "unknow@user.org"
    else
        return substitute(l:user_email, "\n", "", "")
endfun

fun! BBHeader()
    let l:current_year = strftime("%Y")
    let l:user_name = <SID>GetUserName()
    let l:user_email = <SID>GetUserEmail()
    0 put ='# Copyright (C) ' . l:current_year .
                \ ' ' . l:user_name . ' <' . l:user_email . '>'
    put ='# Released under the MIT license (see COPYING.MIT for the terms)'
    $
endfun

fun! NewBBTemplate()
    let l:paste = &paste
    set nopaste
    
    " Get the header
    call BBHeader()

    " New the bb template
    put ='DESCRIPTION = \"\"'
    put ='HOMEPAGE = \"\"'
    put ='LICENSE = \"\"' 
    put ='SECTION = \"\"'
    put ='DEPENDS = \"\"'
    put =''
    put ='SRC_URI = \"\"'

    " Go to the first place to edit
    0
    /^DESCRIPTION =/
    exec "normal 2f\""

    if paste == 1
        set paste
    endif
endfun

if !exists("g:bb_create_on_empty")
    let g:bb_create_on_empty = 1
endif

" disable in case of vimdiff
if v:progname =~ "vimdiff"
    let g:bb_create_on_empty = 0
endif

augroup NewBB
    au BufNewFile *.bb
                \ if g:bb_create_on_empty |
                \    call NewBBTemplate() |
                \ endif
augroup END

