interface Props {
  count: number;
}

const FibonacciCount = ({ count }: Props) => {
  return (
    <>
      <div className="mx-auto">
        <div className="stat">
          <div className="stat-title mb-2">Fibonacci Count</div>
          <div className="stat-value">{count}</div>
        </div>
      </div>
    </>
  );
};

export default FibonacciCount;
