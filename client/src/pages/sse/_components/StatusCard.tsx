import type { SseStatus } from "../../_types/types";

interface Props {
  status: SseStatus;
}

export const StatusCard = ({ status }: Props) => {
  return (
    <>
      <>
        <div className="stat">
          <div className="stat-title">Status    </div>
          <div className="stat-value">{status}</div>
        </div>
      </>
    </>
  );
};
