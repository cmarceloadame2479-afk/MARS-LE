.data
message: .asciiz "You liberated a pokemon!"
lose: .asciiz "You failed to liberate a pokemon!"
.text

enc
bat
libsuc win
tra lose
jump End


win:
tra message

End:
