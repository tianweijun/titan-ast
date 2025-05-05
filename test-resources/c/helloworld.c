#include<stdio.h>

int main(){
    int n = 100;
    printf( "0+1+...+%d=%d\n", n, sum(n) );
    return 0;
}

int sum( int n ){
    int sum = 0;

    for ( int number = 0 ; number <= n ; number++ ){
        sum += number;
    }

return sum;
}/**/