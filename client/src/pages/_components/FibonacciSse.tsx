import ConnectionStatus from "./ConnectionStatus";
import DatasourceRadio from "./DatasourceRadio";
import FibonacciCount from "./FibonacciCount";
import { useSse } from "../_hooks/useSse";

const username = "mangila";

const FibonacciSse = () => {
  const { sseStatus, list, liveData } = useSse(username);

  return (
    <>
      <div className="p-4 border rounded-lg shadow-sm">
        <div className="grid grid-cols-1 md:grid-cols-3">
          <ConnectionStatus sseStatus={sseStatus} />
          <DatasourceRadio username={username} />
          <FibonacciCount count={list.length} />
        </div>
      </div>
      {liveData}
      {list}
    </>
  );
};

export default FibonacciSse;
