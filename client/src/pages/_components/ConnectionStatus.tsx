import type { SseStatus } from "../_types/types";

interface Props {
  sseStatus: SseStatus;
}

const ConnectionStatus = ({ sseStatus }: Props) => {
  switch (sseStatus) {
    case "offline":
      return <div className="badge badge-neutral">{sseStatus}</div>;
    case "connecting":
      return <div className="badge badge-info">{sseStatus}</div>;
    case "open":
      return <div className="badge badge-success">{sseStatus}</div>;
    case "error":
      return <div className="badge badge-error">{sseStatus}</div>;
  }
};

export default ConnectionStatus;
