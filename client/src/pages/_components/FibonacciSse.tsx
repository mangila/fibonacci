import ConnectionStatus from "./ConnectionStatus";
import DatasourceRadio from "./DatasourceRadio";
import FibonacciCount from "./FibonacciCount";
import { useSse } from "../_hooks/useSse";

const username = "mangila";

const FibonacciSse = () => {
  const { status, data } = useSse(username);

  return (
    <>
      <div className="p-4 border rounded-lg shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-3">
          <ConnectionStatus status={status} />
          <DatasourceRadio />
          <FibonacciCount count={12} />
        </div>
      </div>
      {data}
    </>
  );
};

export default FibonacciSse;
