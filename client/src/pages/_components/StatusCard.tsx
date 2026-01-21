import type { ConnectionStatus } from "../_types/types";

interface Props {
  status: ConnectionStatus;
}

export const StatusCard = ({ status }: Props) => {
  return (
    <>
      <>
        <div className="stat">
          <div className="stat-title">Status </div>
          <div className="stat-value">{status}</div>
        </div>
      </>
    </>
  );
};
