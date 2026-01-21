interface Props {
  count: number;
}

export const CountCard = ({ count }: Props) => {
  return (
    <>
      <div className="stat">
        <div className="stat-title">Count</div>
        <div className="stat-value">{count}</div>
      </div>
    </>
  );
};
