fn fib(n: int) -> int {
	if n < 0 { return -1; }
	else if n == 0 { return 0; }
	else if n == 1 { return 1; }
	else { return fib(n-2) + fib(n-1); }
}

fn main() -> int {
	let i: int = 0;
	let max: int = 0;
	max = getint();
	if max > 20 { max = 21; }
	else if max < 0 {
		putint(-1);
		return 0;
	}
	else {}
	while i <= max {
		putint(fib(i));
        putln();
		i = i+1;
	}
	return 0;
}
