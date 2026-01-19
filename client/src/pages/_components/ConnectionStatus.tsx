import type { Status } from "../_types/types";

interface Props {
  status: Status;
}

const ConnectionStatus = ({ status }: Props) => {
  return (
    <>
      {status ? (
        <>
          <div className="badge badge-success">Connected</div>
        </>
      ) : (
        <>
          <div className="badge badge-neutral">Offline</div>
        </>
      )}
    </>
  );
};

export default ConnectionStatus;
