void ParalleledMatrixMultiplicationMS( double[,] a, double[,] b, double[,] c )
{
    int s = a.GetLength( 0 );

    System.Threading.Parallel.For( 0, s, delegate( int i )
    {
        for ( int j = 0; j < s; j++ )
        {
            double v = 0;

            for ( int k = 0; k < s; k++ )
            {
                v += a[i, k] * b[k, j];
            }

            c[i, j] = v;
        }
    } );
}