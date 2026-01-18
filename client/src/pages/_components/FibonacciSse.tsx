import ConnectionStatus from "./ConnectionStatus";
import DatasourceRadio from "./DatasourceRadio";
import FibonacciCount from "./FibonacciCount";

const FibonacciSse = () => {
  return (
    <>
      <div className="p-4 border rounded-lg shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-3">
          <ConnectionStatus status={false} />
          <DatasourceRadio />
          <FibonacciCount count={12} />
        </div>
      </div>
    </>
  );
};

export default FibonacciSse;
