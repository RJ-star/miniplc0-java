fn main() -> void {
    let c: int = 0;
    let i: int;
    while c < 10 {
        i = 0;
        while i < 9 - c {
            putchar(32);
            i = i + 1;
        }
        i = 0;
        while i < 2 * c + 1 {
            putchar(42);
            i = i + 1;
        }
        putln();
        c = c + 1;
    }
}
