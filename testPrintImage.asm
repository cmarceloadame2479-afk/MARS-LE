.data
filename:   .asciiz "B2W2_Team_Plasma.bmp"
err_msg:    .asciiz "File error\n"

.align 2
header:     .space 54
buffer:     .space 4000000     # large safe buffer

.text
.globl main

main:

# =========================
# OPEN FILE
# =========================
li   $v0, 13
la   $a0, filename
li   $a1, 0
li   $a2, 0
syscall

move $s0, $v0
bltz $s0, file_error

# =========================
# READ HEADER
# =========================
li   $v0, 14
move $a0, $s0
la   $a1, header
li   $a2, 54
syscall

la   $t0, header

# =========================
# FILE SIZE (bytes 2–5)
# =========================
lbu  $t1, 2($t0)
lbu  $t2, 3($t0)
lbu  $t3, 4($t0)
lbu  $t4, 5($t0)

sll  $t2, $t2, 8
sll  $t3, $t3, 16
sll  $t4, $t4, 24

or   $s6, $t1, $t2
or   $s6, $s6, $t3
or   $s6, $s6, $t4     # file size

# =========================
# PIXEL OFFSET
# =========================
lbu  $t1, 10($t0)
lbu  $t2, 11($t0)
lbu  $t3, 12($t0)
lbu  $t4, 13($t0)

sll  $t2, $t2, 8
sll  $t3, $t3, 16
sll  $t4, $t4, 24

or   $s3, $t1, $t2
or   $s3, $s3, $t3
or   $s3, $s3, $t4

# =========================
# WIDTH (REAL)
# =========================
lbu  $t1, 18($t0)
lbu  $t2, 19($t0)
lbu  $t3, 20($t0)
lbu  $t4, 21($t0)

sll  $t2, $t2, 8
sll  $t3, $t3, 16
sll  $t4, $t4, 24

or   $s4, $t1, $t2
or   $s4, $s4, $t3
or   $s4, $s4, $t4

# =========================
# HEIGHT (REAL)
# =========================
lbu  $t1, 22($t0)
lbu  $t2, 23($t0)
lbu  $t3, 24($t0)
lbu  $t4, 25($t0)

sll  $t2, $t2, 8
sll  $t3, $t3, 16
sll  $t4, $t4, 24

or   $s5, $t1, $t2
or   $s5, $s5, $t3
or   $s5, $s5, $t4

# =========================
# READ FULL FILE (LOOP)
# =========================
la   $t0, buffer
move $t1, $s6      # remaining bytes

read_loop:
beqz $t1, read_done

li   $v0, 14
move $a0, $s0
move $a1, $t0
move $a2, $t1
syscall

blez $v0, read_done

add  $t0, $t0, $v0
sub  $t1, $t1, $v0

j read_loop

read_done:

# close file
li   $v0, 16
move $a0, $s0
syscall

# =========================
# FRAMEBUFFER BASE
# =========================
li   $s7, 0x10010000

# display size
li   $s1, 256
li   $s2, 256

# =========================
# ROW SIZE
# =========================
mul  $t0, $s4, 3
addi $t0, $t0, 3
andi $t0, $t0, -4
move $s0, $t0

# =========================
# Y LOOP
# =========================
li $t8, 0

y_loop:
bge $t8, $s2, done

# scale Y
mul  $t6, $t8, $s5
div  $t6, $s2
mflo $t6

# flip BMP
sub  $t6, $s5, $t6
addi $t6, $t6, -1

# row start
mul  $t4, $t6, $s0
la   $t1, buffer
add  $t1, $t1, $s3
add  $t1, $t1, $t4

li $t3, 0

x_loop:
bge $t3, $s1, next_row

# scale X
mul  $t9, $t3, $s4
div  $t9, $s1
mflo $t9

# pixel addr
mul  $t0, $t9, 3
add  $t2, $t1, $t0

# BGR → RGB
lbu $t5, 0($t2)
lbu $t6, 1($t2)
lbu $t7, 2($t2)

sll $t7, $t7, 16
sll $t6, $t6, 8
or  $t9, $t7, $t6
or  $t9, $t9, $t5

# framebuffer write
mul  $a0, $t8, $s1
add  $a0, $a0, $t3
sll  $a0, $a0, 2
add  $a0, $a0, $s7

sw $t9, 0($a0)

addi $t3, $t3, 1
j x_loop

next_row:
addi $t8, $t8, 1
j y_loop

done:
li $v0, 10
syscall

file_error:
li $v0, 4
la $a0, err_msg
syscall
li $v0, 10
syscall