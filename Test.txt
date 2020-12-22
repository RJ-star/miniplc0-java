fn gcd(a: int, b: int) -> int {
    if a > b {
        return gcd(b, a);
    } else if a == 0 {
        return b;
    } else {
        return gcd(b - a, a);
    }
}

fn main() -> void {
    let count: int;
    let a: int;
    let b: int;

    count = getint();
    while count > 0 {
        a = getint();
        b = getint();
        putint(gcd(a, b));
        putln();
        count = count - 1;
    }
}
