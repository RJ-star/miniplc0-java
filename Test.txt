let i: int = 2;

fn foo() -> int {
    let i: int = 1;
    putint(i);
    putln();
    return i;
}

fn bar() -> void {
    putint(i);
    putln();
    i = i + 1;
}

fn main() -> void {
    let i: int = 1;
    putint(i);
    putln();
    putint(foo());
    putln();
    bar();
    bar();
    bar();
    putint(foo());
    putln();
}
